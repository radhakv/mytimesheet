/*
 * ScalaTetrix.scala
 */

package ScalaTetrix

import swing._
import event._
import java.awt.{Dimension, Graphics2D, Graphics, Image, Rectangle}
import java.awt.{Color => AWTColor}
import java.awt.event.{KeyListener, KeyEvent, ActionEvent}
import javax.swing.{Timer => SwingTimer, AbstractAction}
import java.util.Random

object App extends SimpleGUIApplication {
  var game = Game.newGame

  /// top
  override def top = frame

  val frame = new MainFrame {
    title = "Scala Tetrix"

    contents = new Panel() {
      background = AWTColor.white
      preferredSize = (380, 600)

      override def paintComponent(g: Graphics) {
        g.setColor(AWTColor.white)
        g.fillRect(0, 0, size.width, size.height)
        onPaint(g.asInstanceOf[Graphics2D])
      }
    } // new Panel()

    // scala-swing 2.7.5 doesn't have KeyPressed event.'
    peer.setFocusable(true)
    peer.addKeyListener(new KeyListener {
      override def keyPressed(e: KeyEvent) {
        onKeyPress(e.getKeyCode)
        repaint()
      }

      override def keyReleased(e: KeyEvent) {}
      override def keyTyped(e: KeyEvent) {}
    })

    val timer = new SwingTimer(1000, new AbstractAction() {
      override def actionPerformed(e: ActionEvent) {
        if (game.mode == ActiveMode) {
          game = game.tick
          repaint()
        } // if
      }
    })

    timer.start
  } // def top new MainFrame

  /// onPaint
  def onPaint(g: Graphics2D) {
    val CELL_SIZE: Int = 20
    val CELL_MARGIN: Int = 1
    val darkRed = new AWTColor(200, 100, 100)

    /// buildRect
    def buildRect(p: Tuple2[Int, Int], board: Board): Rectangle =
      new Rectangle(p._1 * (CELL_SIZE + CELL_MARGIN) + board.pos._1,
        (board.size._2 - p._2 - 1) * (CELL_SIZE + CELL_MARGIN) + board.pos._2,
        CELL_SIZE,
        CELL_SIZE)

    /// drawBoard
    def drawBoard(board: Board) {
      g.setColor(AWTColor.gray)

      for (p <- board.coordinates if !board.cells.contains(p)) {
        g draw buildRect(p, board)
      } // for p

      board.cells.keys.foreach(g fill buildRect(_, board))
    }

    /// drawBlock
    def drawBlock(block: Block, board: Board) {
      g.setColor(darkRed)
      block.foreach(g fill buildRect(_, board))
    }

    drawBoard(game.board)
    drawBlock(game.block, game.board)
    drawBoard(game.miniBoard)
  }

  def process(f: Game => Option[Game]) =
    f(game) match {
      case Some(e) => e
      case None => game
    }

  /// onKeyPress
  def onKeyPress(keyCode: Int) = keyCode match {
    case KeyEvent.VK_LEFT => game = process(_.moveBy(-1, 0))
    case KeyEvent.VK_RIGHT => game = process(_.moveBy(1, 0))
    case KeyEvent.VK_UP => game = process(_.rotate)
    case KeyEvent.VK_DOWN => game = game.tick
    case KeyEvent.VK_SPACE => game = game.drop
    case _ =>
  }
} // object App

abstract class GameMode
case object NewMode extends GameMode
case object ActiveMode extends GameMode
case object GameOverMode extends GameMode

object Game {
  val BOARD_SIZE = (9, 20)
  val BOARD_POS = (20, 20)
  val MINI_SIZE = (5, 5)
  val MINI_POS = (250, 20)

  /// newGame
  def newGame =
    new Game(
      new Board(BOARD_SIZE, BOARD_POS),
      initBlock(Block.randomBlock(), BOARD_SIZE),
      initBlock(Block.randomBlock(), MINI_SIZE),
      NewMode
    )

  /// initBlock
  def initBlock(block: Block, size: Tuple2[Int, Int]) =
    block.moveTo(size._1 / 2, size._2 - 3)
}

class Game(
  val board: Board,
  val block: Block,
  val nextBlock: Block,
  val mode: GameMode
) {
  val miniBoard =
    new Board(Game.MINI_SIZE, Game.MINI_POS).set(nextBlock)

  /// tick
  def tick: Game = synchronized {
    moveBy(0, -1) match {
      case Some(game) => game
      case None => hitTheFloor
    }
  }

  /// hitTheFloor
  def hitTheFloor: Game = {
    var newBoard = board.checkRows
    val newBlock = Game.initBlock(nextBlock, Game.BOARD_SIZE)
    val newNextBlock = Game.initBlock(Block.randomBlock(), Game.MINI_SIZE)

    var newMode = mode
    if (!newBoard.isInBound(newBlock)
        || newBoard.isCollide(newBlock)) {
      newMode = GameOverMode
    } else {
      newBoard = newBoard + newBlock
    } // if-else

    new Game(newBoard, newBlock, newNextBlock, newMode)
  }

  /// drop
  def drop: Game =
    moveBy(0, -1) match {
      case None => this
      case Some(e) => e.drop
    }

  /// moveBy
  def moveBy(delta: Tuple2[Int, Int]): Option[Game] =
    transform(_.moveBy(delta))

  /// rotate
  def rotate: Option[Game] =
    transform(_.rotate(-Math.Pi / 2.0))

  /// transform
  def transform(f: Block => Block): Option[Game] = {
    if (mode != ActiveMode && mode != NewMode) {
      return None
    } // if

    val newMode = if (mode == NewMode) {
      ActiveMode
    } else {
      mode
    } // if-else

    board.transform(block, f) match {
      case (None, e) => None
      case (Some(newBoard), newBlock) =>
        Some(new Game(newBoard, newBlock, nextBlock, newMode))
    }
  }

}

class Board(
  val size: Tuple2[Int, Int],
  val pos: Tuple2[Int, Int],
  val cells: Map[Tuple2[Int, Int], BlockType]
) {
  def this(size: Tuple2[Int, Int], pos: Tuple2[Int, Int]) = {
    this(size, pos, Map.empty)
  }

  /// coordinates
  def coordinates =
    for (y <- 0 until size._2; x <- 0 until size._1)
      yield(x, y)

  /// clear
  def clear() =
    new Board(size, pos)

  /// trasform
  def transform(
    block: Block,
    f: Block => Block) = {
    val unloadedBoard = this - block
    val transformedBlock = f(block)
    if (!unloadedBoard.isInBound(transformedBlock)
        || unloadedBoard.isCollide(transformedBlock)) {
      (None, block)
    } else {
      (Some(unloadedBoard + transformedBlock), transformedBlock)
    } // if-else
  }

  /// +
  def +(block: Block): Board = {
    assert(!isCollide(block) && isInBound(block))

    def loadList(board: Board, xs: List[Tuple2[Int, Int]]): Board =
      xs match {
        case List() => board
        case x :: tail => loadList(board.set(x, block.blockType), tail)
      }

    loadList(this, block.coordinates)
  }

  /// -
  private def -(block: Block): Board = {
    assert(isInBound(block))

    def unloadList(board: Board, xs: List[Tuple2[Int, Int]]): Board =
      xs match {
        case List() => board
        case x :: tail => unloadList(board.unset(x), tail)
      }

    unloadList(this, block.coordinates)
  }

  /// isRowFilled
  private def isRowFilled(y: Int): Boolean = {
    val row = for (x <- 0 until size._1)
      yield (x, y)
    row forall (cells.contains(_))
  }

  /// removeRow
  private def removeRow(y: Int): Board = {
    var newBoard = this
    for (y <- y until size._2 - 1; x <- 0 until size._1) {
      newBoard = if (newBoard.cells.contains((x, y + 1))) {
        newBoard.set((x, y), newBoard.cells((x, y + 1)))
      } else {
        newBoard.unset((x, y))
      } // if-else
    } // x, y

    for (x <- 0 until size._1) {
      newBoard = newBoard.unset((x, size._2 - 1))
    } // x, y

    return newBoard
  }

  /// checkRows
  def checkRows: Board = {
    var newBoard = this
    for (i <- 0 until size._2) {
      val y = size._2 - 1 - i
      if (newBoard.isRowFilled(y)) {
        newBoard = newBoard.removeRow(y)
      }
    } // i
    return newBoard
  }

  /// isCollide
  def isCollide(block: Block): Boolean =
    block exists (cells.contains(_))

  /// isInBound
  def isInBound(block: Block): Boolean =
    block forall (p => p._1 >= 0 && p._1 < size._1
      && p._2 >= 0 && p._2 < size._2)

  /// set
  def set(block: Block): Board =
    clear + block

  /// set
  def set(key: Tuple2[Int, Int], value: BlockType) =
    new Board(size, pos, cells + (key -> value))

  /// unset
  def unset(key: Tuple2[Int, Int]) =
    new Board(size, pos, cells - key)
}

sealed abstract class BlockType
case object Tee extends BlockType
case object Bar extends BlockType
case object Box extends BlockType
case object El extends BlockType
case object Jay extends BlockType
case object Es extends BlockType
case object Zee extends BlockType

object Block {
  val blockTypes: List[BlockType] = List(Tee, Bar, Box, El, Jay, Es, Zee)
  private val random = new Random()

  /// randomBlock
  def randomBlock(): Block = {
    val blockType = blockTypes(random.nextInt(blockTypes.size));
    new Block(blockType,
      (4, 10),
      blockType match {
        case Tee => List((0.0, 0.0), (-1.0, 0.0), (1.0, 0.0), (0.0, 1.0))
        case Bar => List((0.0, -1.5), (0.0, -0.5), (0.0, 0.5), (0.0, 1.5))
        case Box => List((-0.5, 0.5), (0.5, 0.5), (-0.5, -0.5), (0.5, -0.5))
        case El => List((0.0, 0.0), (0.0, 1.0), (0.0, -1.0), (1.0, -1.0))
        case Jay => List((0.0, 0.0), (0.0, 1.0), (0.0, -1.0), (-1.0, -1.0))
        case Es => List((-0.5, 0.0), (0.5, 0.0), (-0.5, 1.0), (0.5, -1.0))
        case Zee => List((-0.5, 0.0), (0.5, 0.0), (-0.5, -1.0), (0.5, 1.0))
      }
    )
  }
}

class Block(
  val blockType: BlockType,
  val pos: Tuple2[Int, Int],
  val locals: List[Tuple2[Double, Double]]
) extends RandomAccessSeq[Tuple2[Int, Int]] {
  /// length
  override def length =
    coordinates.length

  /// apply
  override def apply(index: Int) =
    coordinates(index)

  /// coordinates
  def coordinates: List[Tuple2[Int, Int]] =
    for (p <- locals)
      yield (Math.round(p._1 + pos._1).asInstanceOf[Int],
        Math.round(p._2 + pos._2).asInstanceOf[Int])

  /// moveBy
  def moveBy(delta: Tuple2[Int, Int]) =
    moveTo((pos._1 + delta._1, pos._2 + delta._2))

  /// moveTo
  def moveTo(newPos: Tuple2[Int, Int]) =
    new Block(blockType, newPos, locals)

  /// rotate
  def rotate(theta: Double) = {
    val s = Math.sin(theta)
    val c = Math.cos(theta)
    val newLocals = for (p <- locals)
      yield (roundToHalf(p._1 * c - p._2 * s),
             roundToHalf(p._1 * s + p._2 * c))
    new Block(blockType, pos, newLocals)
  }

  /// roundToHalf
  private def roundToHalf(value: Double) =
    Math.round(value * 2.0) * 0.5
}
