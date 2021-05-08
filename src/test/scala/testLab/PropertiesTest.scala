package testLab

import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{exists, forAll}

object PropertiesTest extends Properties ("Seqs") {
  def associativeAppendProp[A:Arbitrary]: Prop = forAll {(xs:Seq[A], ys:Seq[A], zs:Seq[A]) =>
    (xs ++ ys) ++ zs == xs ++ (ys ++ zs)
  }
  def identityAppendProp[A:Arbitrary]: Prop = forAll {(xs:Seq[A]) =>
    Seq.empty ++ xs == xs
  }
  def mapIdentityProp[A:Arbitrary]: Prop = forAll {(xs:Seq[A]) =>
    xs.map(identity) == xs
  }

  private case class Type1[T](t:T)
  private case class Type2[T](t:T)
  private val g = (v:Any) => Type1(v)
  private val f = (t:Type1[Any]) => Type2(t.t)
  def composeProperty[A:Arbitrary]: Prop = forAll {
    (xs:Seq[A]) => xs.map(f compose g) == xs.map(g).map(f)
  }

  private val palindromesGen = for {
    s <- Gen.alphaStr
    c <- Gen.option(Gen.alphaChar)
  } yield s ++ c ++ s.reverse

  property("Associative property of append") = Prop.all(
    associativeAppendProp[Int],
    associativeAppendProp[String],
    associativeAppendProp[(Boolean, Double)]
  )
  property("Identity property of append") = Prop.all(
    identityAppendProp[Int],
    identityAppendProp[String],
    identityAppendProp[(Boolean, Double)]
  )
  property("Map Identity property") = Prop.all(
    mapIdentityProp[Int],
    mapIdentityProp[String],
    mapIdentityProp[(Boolean, Double)]
  )
  property("Compose") = Prop.all(
    composeProperty[Int],
    composeProperty[String],
    composeProperty[(Boolean, Double)]
  )
  property("Palindrome") = Prop.forAll(palindromesGen) { s =>
    s == s.reverse
  }
}