package testLab

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import cucumber.api.scala.{EN, ScalaDsl}
import org.junit.Assert
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.runner.RunWith

class ShoppingSteps extends ScalaDsl with EN {
  var (wh, suppliedProds, gotProds) =
    (new BasicWarehouse, Map.empty[Product, Int], Map.empty[Product, Int])

  Given("""^An empty warehouse$""") {
    wh = new BasicWarehouse
    suppliedProds = Map.empty
    gotProds = Map.empty
  }
  Given("""^(\d+) products named (\w+)$""") { (qty:Int, name:String) =>
    suppliedProds = suppliedProds + (Product(name) -> (suppliedProds.getOrElse(Product(name), 0) + qty))
  }
  When("""^I supply the warehouse with the products$"""){
    suppliedProds.foreach { case (prod, qty) => wh.supply(prod, qty) }
  }
  When("""^I try to get (\d+) (\w+)$""") { (qty:Int, name:String) =>
    val got = wh.get(Product(name), qty)
    gotProds = gotProds + (Product(name) -> (gotProds.getOrElse(got._1, 0) + got._2))
  }
  Then("""^I should obtain (\d+) (\w+)$"""){ (qty:Int, name:String) =>
    assertTrue(gotProds.contains(Product(name)))
    assertEquals(qty, gotProds(Product(name)))
  }
  Then("""^I should obtain nothing$"""){
    assertTrue(gotProds.values.forall(_==0))
  }
}
@RunWith(classOf[Cucumber])
@CucumberOptions(features = Array("classpath:features/Shopping.feature"),
  tags = Array("not @Wip"), glue = Array("classpath:testLab"),
  plugin = Array("pretty", "html:target/cucumber/html"))
class CucumberTest {

}
