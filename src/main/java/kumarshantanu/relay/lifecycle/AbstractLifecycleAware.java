package kumarshantanu.relay.lifecycle;

import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public abstract class AbstractLifecycleAware implements LifecycleAware {
    
    private final String name;
    private LifecycleState daemonState = new LifecycleState();
    
    public AbstractLifecycleAware(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return name==null? getClass().getSimpleName(): name.toString();
    }
    
    public LifecycleStateEnum getState() {
        synchronized (daemonState) {
            return daemonState.get();
        }
    }
    
    protected void setState(LifecycleStateEnum newState) {
        synchronized (daemonState) {
            daemonState.set(newState);
        }
    }
    
    public void resume() {
        synchronized (daemonState) {
            if (daemonState.isResumable()) {
                daemonState.set(LifecycleStateEnum.RUNNING);
            }
        }
    }
    
    public void stop() {
        synchronized (daemonState) {
            if (daemonState.isStoppable()) {
                daemonState.set(LifecycleStateEnum.STOPPED);
            }
        }
    }
    
    public void forceStop() {
        synchronized (daemonState) {
            if (daemonState.isStoppable()) {
                daemonState.set(LifecycleStateEnum.FORCE_STOPPED);
            }
        }
    }
    
    public void suspend() {
        synchronized (daemonState) {
            if (daemonState.isSuspendable()) {
                daemonState.set(LifecycleStateEnum.SUSPENDED);
            }
        }
    }
    
    public final void run() {
        setState(LifecycleStateEnum.RUNNING);
        try {
            execute();
        } finally {
            setState(LifecycleStateEnum.STOPPED);
        }
    }
    
    public abstract void execute();
    
    @Override
    public String toString() {
        return name==null? this.getClass().getName(): name.toString();
    }
    
}