/**
 */
package com.opengda.detector.electronalyser.server.model.regiondefinition.tests;

import com.opengda.detector.electronalyser.server.model.regiondefinition.RegiondefinitionFactory;
import com.opengda.detector.electronalyser.server.model.regiondefinition.Sequence;

import junit.framework.TestCase;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Sequence</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class SequenceTest extends TestCase {

	/**
	 * The fixture for this Sequence test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Sequence fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(SequenceTest.class);
	}

	/**
	 * Constructs a new Sequence test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SequenceTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Sequence test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Sequence fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Sequence test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Sequence getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(RegiondefinitionFactory.eINSTANCE.createSequence());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //SequenceTest
