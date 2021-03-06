package dk.bayes.clustergraph.factor

import org.junit._
import org.junit.Assert._
import dk.bayes.clustergraph.testutil.AssertUtil._
import dk.bayes.clustergraph.factor.SingleFactor
import dk.bayes.clustergraph.factor.Var
import dk.bayes.clustergraph.factor.MultiFactor

class MultiFactorTest {

  val factor = MultiFactor(Var(1, 3), Var(2, 2), Array(0.05, 0.1, 0.15, 0.2, 0.225, 0.275))

  /**
   * Tests for constructor
   */

  @Test(expected = classOf[IllegalArgumentException]) def create_values_inconsistent_with_dim_size:Unit = {
    MultiFactor(Var(1, 3), Var(2, 2), Array(0.05, 0.1, 0.15, 0.2, 0.225))
  }

  /**
   * Tests for getVariables() method.
   */
  @Test(expected = classOf[IllegalArgumentException]) def getValue_empty_assignment:Unit = {
    factor.getValue(Array())
  }

  @Test(expected = classOf[IllegalArgumentException]) def getValue_partial_assignment:Unit = {
    factor.getValue(Array(1))
  }

  @Test(expected = classOf[IllegalArgumentException]) def getValue_assignment_value_out_of_range:Unit = {
    factor.getValue(Array(1, 2))
  }

  @Test def getValue = assertEquals(0.15, factor.getValue(Array(1, 0)), 0)

  @Test def getVariables = assertEquals(Array(Var(1, 3), Var(2, 2)).toList, factor.getVariables.toList)

  /**
   * Tests for getValue() and getValues() methods.
   */
  @Test def getValues = assertEquals(Array(0.05, 0.1, 0.15, 0.2, 0.225, 0.275).toList, factor.getValues.toList)

  /**
   * Tests for product() method.
   */
  @Test(expected = classOf[IllegalArgumentException]) def product_single_factor_variable_not_found:Unit = {
    val thatFactor = SingleFactor(Var(3, 3), Array(0.3, 0.5, 0.2))
    factor.product(thatFactor)
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_factor_variable_ids_not_consistent:Unit = {
    val thatFactor = SingleFactor(Var(2, 3), Array(0.3, 0.5, 0.2))
    factor.product(thatFactor)
  }

  @Test(expected = classOf[IllegalArgumentException]) def product_factor_variable_dimensions_not_consistent:Unit = {
    val thatFactor = SingleFactor(Var(1, 2), Array(0.3, 0.5))
    factor.product(thatFactor)
  }

  @Test def product_variable_1:Unit = {
    val thatFactor = new SingleFactor(Var(1, 3), Array(0.4, 0.1, 0.5))
    val factorProduct = factor.product(thatFactor)

    assertFactor(MultiFactor(Var(1, 3), Var(2, 2), Array(0.02, 0.04, 0.015, 0.02, 0.1125, 0.1375)), factorProduct, 0.0001)
  }

  @Test def product_variable_2:Unit = {
    val thatFactor = new SingleFactor(Var(2, 2), Array(0.2, 0.8))
    val factorProduct = factor.product(thatFactor)

    assertFactor(MultiFactor(Var(1, 3), Var(2, 2), Array(0.01, 0.08, 0.03, 0.16, 0.045, 0.22)), factorProduct, 0.0001)
  }

  /**
   * Tests for withEvidence() method.
   */

  @Test(expected = classOf[IllegalArgumentException]) def withEvidence_variable_not_found:Unit = {
    factor.withEvidence(5, 1)
  }

  @Test(expected = classOf[IllegalArgumentException]) def withEvidence_variable_value_index_out_of_range:Unit = {
    factor.withEvidence(1, 3)
  }

  @Test def withEvidence:Unit = {

    val factorWithEvidence = factor.withEvidence(1, 2)
    val expectedFactor = MultiFactor(Var(1, 3), Var(2, 2), Array(0, 0, 0, 0, 0.225, 0.275))
    assertFactor(expectedFactor, factorWithEvidence)
  }

  /**
   * Tests for marginal() method.
   */
  @Test(expected = classOf[IllegalArgumentException]) def marginal_variable_not_found:Unit = {
    factor.marginal(3)
  }

  @Test def marginal:Unit = {

    val marginalFactor = factor.marginal(2)
    val expectedMarginalFactor = SingleFactor(Var(2, 2), Array(0.425, 0.575))

    assertFactor(expectedMarginalFactor, marginalFactor, 0.0001)
  }

  /**
   * Tests for normalise() method.
   */
  @Test def normalise_aready_normalised:Unit = {

    val factor = MultiFactor(Var(1, 3), Var(2, 2), Array(0.05, 0.1, 0.15, 0.2, 0.225, 0.275))
    val normalisedFactor = factor.normalise()

    assertFactor(factor, normalisedFactor)
  }

  @Test def normalise:Unit = {

    val factor = MultiFactor(Var(1, 3), Var(2, 2), Array(0.05, 0.1, 0.15, 0, 0.225, 0.275))
    val normalisedFactor = factor.normalise()

    assertFactor(MultiFactor(Var(1, 3), Var(2, 2), Array(0.0625, 0.125, 0.1874, 0, 0.28125, 0.3437)), normalisedFactor, 0.0001)
  }

  /**
   * Tests for mapAssignments
   */

  @Test def mapAssignments:Unit = {
    val factor = MultiFactor(Var(1, 3), Var(2, 2), Array(0.05, 0.1, 0.15, 0, 0.225, 0.275))

    val assignments = factor.mapAssignments(f => f)

    assertEquals(List(0, 0), assignments(0).toList)
    assertEquals(List(0, 1), assignments(1).toList)
    assertEquals(List(1, 0), assignments(2).toList)
    assertEquals(List(1, 1), assignments(3).toList)
    assertEquals(List(2, 0), assignments(4).toList)
    assertEquals(List(2, 1), assignments(5).toList)
  }

}