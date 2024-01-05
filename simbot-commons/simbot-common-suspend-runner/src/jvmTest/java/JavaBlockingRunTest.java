import love.forte.simbot.suspendrunner.reserve.SuspendReserves;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author ForteScarlet
 */
public class JavaBlockingRunTest {

    private static final String EXPECT_NAME = "forte";

    private void checkDuration(long startNano) {
        final var now = System.nanoTime();
        Assertions.assertTrue((now - startNano) > TimeUnit.MILLISECONDS.toNanos(100));
    }

    @Test
    public void blockingRun() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var name = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.block());
        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void asyncRun() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameFuture = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.async());
        final var name = nameFuture.join();

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void monoRun() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameMono = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.mono());
        final var name = nameMono.block(Duration.ofMillis(150L));

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void rx2Run() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameMaybe = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.rx2Maybe());
        final var name = nameMaybe.blockingGet();

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void rx3Run() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameMaybe = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.rx3Maybe());
        final var name = nameMaybe.blockingGet();

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

}
