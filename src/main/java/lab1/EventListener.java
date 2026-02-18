package lab1;

@FunctionalInterface
public interface EventListener {
    void pushEvent(FarmEvent event);
}
