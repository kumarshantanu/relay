package kumarshantanu.relay.lifecycle;

import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public interface LifecycleAware {
    
    public LifecycleStateEnum getState();
    
    public void suspend();
    
    public void resume();
    
    public void stop();
    
    public void forceStop();
    
}