package view.util;

import oracle.adf.controller.v2.lifecycle.Lifecycle;
import oracle.adf.controller.v2.lifecycle.PageController;
import oracle.adf.controller.v2.lifecycle.PagePhaseEvent;
import oracle.adf.controller.v2.lifecycle.PagePhaseListener;
import oracle.adf.controller.v2.context.LifecycleContext;

import oracle.adf.model.BindingContext;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

public class MyAdfListener extends PageController implements PagePhaseListener {
    public MyAdfListener() {
        super();
    }

    public void afterPhase(PagePhaseEvent pagePhaseEvent) {
        
    }

    public void beforePhase(PagePhaseEvent pagePhaseEvent) {
    }
    
    @Override
    public void prepareRender(LifecycleContext lc){
        try{
            super.prepareModel(lc);
            BindingContainer bc = BindingContext.getCurrent().getCurrentBindingsEntry();
            OperationBinding oper = bc.getOperationBinding("findOLDFileList"); 
            oper.execute();
            
            oper = bc.getOperationBinding("findFileList"); 
            oper.execute();
            
        } catch (Exception ex){
            ex.printStackTrace();
            if (ex instanceof NullPointerException){
                System.out.println("QUAAUAUAUAUAUUAUAU");
            }
        }
    }

}
