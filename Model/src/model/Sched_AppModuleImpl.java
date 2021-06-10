package model;

import model.common.Sched_AppModule;

import model.commons.Parent_AppModuleImpl;

import oracle.jbo.server.ViewObjectImpl;


//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Sched_AppModuleImpl extends Parent_AppModuleImpl implements Sched_AppModule {
	/**
	 *
	 *  This is the default constructor (do not remove)
	 */
	public Sched_AppModuleImpl() {
	}

	/**
	 *
	 *  Sample main for debugging Business Components code using the tester.
	 */
	public static void main(String[] args) {
		launchTester("model", "Sched_AppModuleLocal");
	}

	/**
	 * Container's getter for Sched_SoCodaProcessiView1.
	 * @return Sched_SoCodaProcessiView1
	 */
	public ViewObjectImpl getSched_RunningProcessesView() {
		return (ViewObjectImpl)findViewObject("Sched_RunningProcessesView");
	}


    /**
     * Container's getter for Sched_TodayRunProcessesView1.
     * @return Sched_TodayRunProcessesView1
     */
    public ViewObjectImpl getSched_TodayRunProcessesView1() {
        return (ViewObjectImpl) findViewObject("Sched_TodayRunProcessesView1");
    }

    /**
     * Container's getter for Sched_SoCodaProcessiView1.
     * @return Sched_SoCodaProcessiView1
     */
    public ViewObjectImpl getSched_ReadCodaProcessiView1() {
        return (ViewObjectImpl) findViewObject("Sched_ReadCodaProcessiView1");
    }

    /**
     * Container's getter for Sched_SoCnfImpexpDaSchedulareView1.
     * @return Sched_SoCnfImpexpDaSchedulareView1
     */
    public ViewObjectImpl getSched_SoCnfImpexpDaSchedulareView1() {
        return (ViewObjectImpl) findViewObject("Sched_SoCnfImpexpDaSchedulareView1");
    }
}
