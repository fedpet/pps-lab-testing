Feature: Warehouse
  Background:
    Given An empty warehouse

  Scenario: I should not be able to get unknown products from the warehouse
    When I try to get 1 Basketball
    Then I should obtain nothing

  Scenario: I want to supply a warehouse and then get the products back
    Given 10 products named Basketball
    And 5 products named TV
    When I supply the warehouse with the products
    And I try to get 2 TV
    Then I should obtain 2 TV

  Scenario: I should not be able to get more products than available
    Given 10 products named Basketball
    When I supply the warehouse with the products
    And I try to get 20 Basketball
    Then I should obtain 10 Basketball