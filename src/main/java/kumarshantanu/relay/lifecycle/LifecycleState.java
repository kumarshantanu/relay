package kumarshantanu.relay.lifecycle;

public class LifecycleState {
    
    public static enum LifecycleStateEnum {
        READY,
        RUNNING,
        SUSPENDED,
        STOPPED,
        FORCE_STOPPED  // temporary state - convert to STOPPED after operation
    }
    
    private volatile LifecycleStateEnum internalState = LifecycleStateEnum.READY;
    
    public synchronized void set(final LifecycleStateEnum newState) {
        internalState = newState;
    }
    
    public synchronized LifecycleStateEnum get() {
        return internalState;
    }
    
    public synchronized boolean isResumable() {
        return (internalState == LifecycleStateEnum.SUSPENDED)? true: false;
    }
    
    public synchronized boolean isSuspendable() {
        return internalState == LifecycleStateEnum.RUNNING? true: false;
    }
    
    public synchronized boolean isStoppable() {
        return (internalState == LifecycleStateEnum.RUNNING ||
                internalState == LifecycleStateEnum.SUSPENDED)? true: false;
    }
    
}