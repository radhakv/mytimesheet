package moten.david.util.expression

import scala.io.Source
import scala.Predef._
import org.junit._

trait Expression {
  
	val  and = " and "
	val  or = " or "
	val  plus = "+"
	val  minus = "-"
	val times= "*"
	val divide = "/"
	val equals = "="
	val gt = ">"
	val gte = ">="
	val lt = "<"
	val lte = "<="
	val not = "not "
  
	private def s = toString
      
	def parens(s:String):String = "(" + s + ")"
  
	def string:String = symbols.toString
 
	implicit def stringToSymbol(value:String) = SimpleSymbol(value)
	implicit def listToSymbolGroup(list:List[Symbol]) = SymbolGroup(list)
	implicit def expressionToSymbol(e:Expression) = e.symbols
 
	def symbols:Symbol =       
		this match {
		  //numeric expressions
		  case Numeric(n) => n.toString()
		  case Named(n) => n
		  case Add(x, Negative(y)) => x.join(minus).join(y)
		  case Add(p @ Multiplicative(x,y),z) => p.join(z)
		  case Add(x,p @ Multiplicative(y,z)) => x.join(plus).join(p)
		  case Add(x,y) => x.join(plus).join(y)
		  case Minus(x,y) => Add(x, Negative(y))
		  case Multiply(x, p @Additive(y,z)) => x.join(times).join(List(p))
		  case Multiply( p @Additive(x,y),z) => List(p.symbols).join(times).join(z)
		  case Multiply(x,y) => x.join(times).join(y)
		  case Divide(x, p @Additive(y,z)) => x.join(divide).join(List(p.symbols))	
		  case Divide( p @Additive(x,y),z) => List(p.symbols).join(divide).join(x)
		  case Divide(x,y) => x.join(divide).join(y)	  
		  case Negative(p @ BinaryNumericOperation(x,y)) => minus.join(List(p.symbols))
		  case Negative(x) => minus.join(x)
		  //boolean expresssions
		  case And(p @ Or(x,y),z) =>p.join(and).join(z) 
		  case And(x,p @ Or(y,z)) =>x.join(and).join(p)
		  case And(x,y)=> x.join(and).join(y)
    	  case Or(x,y)=> x.join(or).join(y)
       	  case GreaterThan(x,y)=> x.join(gt).join(y)
          case GreaterThanOrEquals(x,y)=> x.join(gte).join(y)
          case LessThan(x,y)=> x.join(lt).join(y)
          case LessThanOrEquals(x,y)=> x.join(lte).join(y)
          case Equals(x,y)=> x.join(equals).join(y)
          case Not(p @ BinaryBooleanOperation(x,y)) => not.join(List(p.symbols))
          case Not(x) => not.join(x)
    	  case _ => SimpleSymbol("?"+this.getClass+"?")
		}
  
	private def simp(e:NumericExpression):NumericExpression  = 
		e match {
		  case Negative(Multiply(x,y)) => simp(Multiply(Negative(x),y))
		  case Negative(Divide(x,y)) => simp(Divide(Negative(x),y))
	      case Negative(Numeric(x)) => Numeric(-x)
	      case Negative(Add(x,y))=>simp(Add(Negative(x),Negative(y)))
	      case Negative(x) => Negative(simp(x))
	      case Add(Numeric(x), Numeric(y)) => Numeric(x+y)
	      case Add(x,Add(y,z)) => simp(Add(Add(x,y),z))
	      case Add(x, Numeric(y)) => simp(Add(Numeric(y),x))
	      case Add(x,y) => Add(simp(x), simp(y))
	      case Minus(x,y) => simp(Add(simp(x), simp(Negative(y))))
	      case Multiply(Numeric(x),Numeric(y)) => Numeric(x*y)
	      case Multiply(Add(x,y),z) => simp(Add(simp(Multiply(x,z)),simp(Multiply(y,z))))
	      case Multiply(z,Add(x,y)) => simp(Add(simp(Multiply(x,z)),simp(Multiply(y,z))))
	      case Multiply(x,y) => Multiply(simp(x), simp(y))
	      case Divide(Numeric(x),Numeric(y)) => Numeric(x/y)
	      case Divide(x,y) => Divide(simp(x), simp(y))
	      case _ =>  e
      }
      
    private def simp(e:BooleanExpression):BooleanExpression  = 
      e match {
	      case GreaterThan(x,y) => GreaterThan(simp(x),simp(y))
	      case GreaterThanOrEquals(x,y) => GreaterThanOrEquals(simp(x),simp(y))
	      case LessThan(x,y) => LessThan(simp(x),simp(y))
	      case LessThanOrEquals(x,y) => LessThanOrEquals(simp(x),simp(y))
	      case Equals(x,y) => Equals(simp(x),simp(y))
	      case _ => e
    }
    
    def simplify():Expression = 
      this match {
	      case p @ NumericExpression() =>  if (this == simp(p)) p else simp(p).simplify
	      case p @ BooleanExpression() =>  if (this == simp(p)) p else simp(p).simplify
    }
    
}

case class NumericExpression() extends Expression {
    def +(e: NumericExpression) = Add(this,e)
    def -(e: NumericExpression) = Minus(this,e)
    def *(e: NumericExpression) = Multiply(this,e)
    def /(e: NumericExpression) = Divide(this,e)
    def >(e:NumericExpression) = GreaterThan(this,e)
    def eq(e:NumericExpression) = Equals(this,e)
    def >=(e:NumericExpression) = GreaterThanOrEquals(this, e)
    def <=(e:NumericExpression) = LessThanOrEquals(this,e)
}

case class BooleanExpression() extends Expression {
	def &&(e:BooleanExpression) = and(e)
	def ||(e:BooleanExpression) = or(e)
	def and(e:BooleanExpression) = new And(this,e)
	def or(e: BooleanExpression) = new Or(this,e)
}

//Numeric expresssions
case class Numeric(num: BigDecimal) extends NumericExpression 
case class Named(name:String) extends NumericExpression
case class BinaryNumericOperation(left: NumericExpression, right:NumericExpression) extends NumericExpression
case class Comparison(left: NumericExpression, right:NumericExpression) extends BooleanExpression
case class Additive(override val left: NumericExpression, override val right:NumericExpression) 
	extends BinaryNumericOperation(left: NumericExpression, right:NumericExpression)
case class Multiplicative(override val left: NumericExpression, override val right:NumericExpression) 
	extends BinaryNumericOperation(left: NumericExpression, right:NumericExpression)
case class Add(override val left: NumericExpression,override val right:NumericExpression) 
	extends Additive(left:NumericExpression, right:NumericExpression) 
case class Minus(override val left: NumericExpression,override val right:NumericExpression) 
	extends Additive(left:NumericExpression, right:NumericExpression) 
case class Divide(override val left: NumericExpression, override val right:NumericExpression) 
	extends Multiplicative(left:NumericExpression, right:NumericExpression)
case class Multiply(override val left: NumericExpression, override val right:NumericExpression) 
	extends Multiplicative(left:NumericExpression, right:NumericExpression)
case class Negative(e:NumericExpression) extends NumericExpression
//Boolean expressions
case class BinaryBooleanOperation(left:BooleanExpression, right:BooleanExpression) 
	extends BooleanExpression 
case class And(override val left:BooleanExpression, override val right:BooleanExpression) 
	extends BinaryBooleanOperation(left:BooleanExpression, right:BooleanExpression)
case class Or(override val left:BooleanExpression, override val right:BooleanExpression) 
	extends BinaryBooleanOperation(left:BooleanExpression, right:BooleanExpression)
case class GreaterThan(override val left:NumericExpression,override val right:NumericExpression) 
	extends Comparison(left:NumericExpression, right:NumericExpression) 
case class GreaterThanOrEquals(override val left:NumericExpression,override val right:NumericExpression) 
	extends Comparison(left:NumericExpression, right:NumericExpression)
case class LessThan(override val left:NumericExpression,override val right:NumericExpression) 
	extends Comparison(left:NumericExpression, right:NumericExpression)
case class LessThanOrEquals(override val left:NumericExpression,override val right:NumericExpression) 
	extends Comparison(left:NumericExpression, right:NumericExpression)
case class Equals(override val left:NumericExpression,override val right:NumericExpression) 
	extends Comparison(left:NumericExpression, right:NumericExpression)
case class Not(e:BooleanExpression) extends BooleanExpression

//Symbols
case class Symbol() {
  def join(symbol:Symbol):Symbol = this match {
    case SimpleSymbol(value) => SymbolGroup(List(this)).join(symbol)
    case p @ SymbolGroup(symbols) => SymbolGroup( symbols ::: List(symbol))
  }
  
  override def toString:String = this match {
    case SimpleSymbol(x) => x
    case SymbolGroup(Nil) => ""
    case SymbolGroup(x) => "(" + x.foldLeft("")(_+_) + ")"
  }
}

case class SymbolGroup( list:List[Symbol]) extends Symbol
case class SimpleSymbol(value:String) extends Symbol

//Main class
object ExpressionUtil {
  
	implicit def bigDecimalToNumericExpression(x: BigDecimal) = Numeric(x)
    implicit def intToNumericExpression(x: Int) = Numeric(BigDecimal(x))
    implicit def doubleToNumericExpression(x: Double) = Numeric(BigDecimal(x))
    implicit def stringToNumericExpression(s: String) = Named(s)
    
    private def assert( expectedResult:String, e:Expression) {
		println("test "+expectedResult +"="+ e.symbols)
		Predef.assert(expectedResult==e.symbols.toString)
    }
    
    def main(args:Array[String]) { 
	  val e = ((Numeric(27) + "total.record.count" + 1) * 100 
            - "partial.record.count" / 34 + (Numeric(90) - 87)/2) >= Numeric(67)
	  println(e)
	  println(e.simplify)
	  println(e.simplify.string)
	  val e2=Numeric(26) + "hello.there" - (Numeric(34) + 56)
   
	  assert("27",Numeric(27))
	  assert("(27+34)",Numeric(27)+ Numeric(34))
      
     
	}
 }