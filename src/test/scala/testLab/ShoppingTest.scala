package testLab

import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, FunSpec, FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class BasicCartTest extends FunSuite with Matchers {
  test("Empty cart should have size zero"){
    new BasicCart() should have size 0
  }
  test("Empty cart should have no price"){
    new BasicCart().totalCost shouldBe 0
  }
  test("Empty cart should have no content"){
    new BasicCart().content shouldBe empty
  }
  test("Adding items should increase size"){
    val cart = new BasicCart()
    val item = Item(Product("prod"), ItemDetails(2, Price(5)))

    cart.add(item)

    cart should have size 1
  }
  test("Adding items should increase price"){
    val cart = new BasicCart()
    val prod = Product("prod")
    val item1 = Item(prod, ItemDetails(2, Price(5)))
    val item2 = Item(prod, ItemDetails(1, Price(3)))

    cart.add(item1)
    cart.add(item2)

    cart.totalCost shouldBe 8
  }
  test("Adding items should modify content"){
    val cart = new BasicCart()
    val item = Item(Product("prod"), ItemDetails(2, Price(5)))

    cart.add(item)

    cart.content should (contain (item) and have size 1)
  }
}



@RunWith(classOf[JUnitRunner])
class BasicCatalogTest extends FunSpec with Matchers {
  describe("A Catalog") {
    describe("when empty") {
      it("should have size 0") {
        assert(new BasicCatalog(Map()).products.isEmpty)
      }
      it("should raise NoSuchElementException when inquiring about the price of an unknown product") {
        assertThrows[NoSuchElementException] {
          new BasicCatalog(Map()).priceFor(Product("test"))
        }
      }
    }
    describe("containing some products") {
      val prods = Map(
        Product("Prod1") -> Price(5),
        Product("Prod2") -> Price(2),
        Product("Prod3") -> Price(1),
      )
      it("should have correct products") {
        assert(new BasicCatalog(prods).products == prods)
      }
      it("should have correct prices") {
        assert(new BasicCatalog(prods).priceFor(Product("Prod1")) == Price(5))
      }
      it("should take qty into account for price calculation") {
        assert(new BasicCatalog(prods).priceFor(Product("Prod1"), 10) == Price(50))
      }
    }
  }
}

@RunWith(classOf[JUnitRunner])
class BasicWarehouseTest extends FlatSpec {
  "Getting unknown items" should "be impossible" in {
    val wh = new BasicWarehouse()
    val prod = Product("Prod")

    assert(wh.get(prod, 10) == (prod, 0))
  }
  "Supplying items" should "allow to get them back" in {
    val wh = new BasicWarehouse()
    val prod = Product("Prod")

    wh.supply(Product("Prod"), 10)

    assert(wh.get(prod, 10) == (prod, 10))
  }
  "Getting more items than available" should "be the same as getting all of them, but not more" in {
    val wh = new BasicWarehouse()
    val prod = Product("Prod")

    wh.supply(Product("Prod"), 10)

    assert(wh.get(prod, 20) == (prod, 10))
  }
}

@RunWith(classOf[JUnitRunner])
class ShoppingTest extends FlatSpec with MockFactory with Matchers {
  "Shopping " should " work" in {
    val warehouse = mock[Warehouse]
    val catalog = mock[Catalog]
    val cart = mock[Cart]
    val logger = mock[Logger]
    val sh = new Shopping(warehouse, catalog, cart, logger)
    val prod = Product("Prod")
    val qty = 10
    val price = Price(10)

    inAnyOrder {
      (logger.log _).expects(*).anyNumberOfTimes()
      ((p:Product,q:Int) => catalog.priceFor(p,q)).expects(prod, qty).returning(price)
      (cart.add _).expects(Item(prod, ItemDetails(qty, price))).once()
      (() => cart.size).expects().returning(1)
      (() => cart.totalCost).expects().returning(price.value)
    }

    inSequence {
      (warehouse.get _).expects(prod, qty).returning((prod, qty)).once()
      (warehouse.get _).expects(prod, qty).returning((prod, 0)).once()
    }

    sh.pick(prod, qty)
    sh.pick(prod, qty)
  }
}