package funFarm.infrastructure.security;

public final class ActorContext {
    private static final ThreadLocal<String> ACTOR = new ThreadLocal<>();

    private ActorContext() {}

    public static void set(String actor) {
        ACTOR.set(actor);
    }

    public static String get() {
        return ACTOR.get();
    }

    public static void clear() {
        ACTOR.remove();
    }
}
