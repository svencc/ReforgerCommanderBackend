package lib.gecom.observer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ObserverTest {

    @Test
    public void notifyObserversWith_sendTestEvent_withObserversRetrieveMessage() {
        // Arrange
        final Subject<TestEvent> testEventSubject = new Subject<>();
        final TestObserver testObserver1 = new TestObserver();
        final TestObserver testObserver2 = new TestObserver();

        // Act
        testObserver1.observe(testEventSubject);
        testObserver2.observe(testEventSubject);

        final Note<TestEvent> testEventNote = new TestEvent("test");
        testEventSubject.notifyObserversWith(testEventNote);

        // Assert
        assertEquals(testEventSubject, testObserver1.notifiedBySubject);
        assertEquals(testEventNote, testObserver1.notifiedWithEvent);

        assertEquals(testEventSubject, testObserver2.notifiedBySubject);
        assertEquals(testEventNote, testObserver2.notifiedWithEvent);
    }

    @Test
    public void reportMyDeath_subjectIsRemovedOnObservers() {
        // Arrange
        final Subject<TestEvent> testEventSubject = new Subject<>();
        final TestObserver testObserver1 = new TestObserver();
        final TestObserver testObserver2 = new TestObserver();

        // Act
        testObserver1.observe(testEventSubject);
        testObserver2.observe(testEventSubject);

        final Note<TestEvent> testEventNote = new TestEvent("test");
        testEventSubject.reportMyDeath();

        // Assert
        assertTrue(testObserver1.getSubject().isEmpty());
        assertTrue(testObserver2.getSubject().isEmpty());
    }

    @Test
    public void observationStoppedThrough_observerGetsRemovedFromObserverList_noNotificationIsSendAnymore() {
        // Arrange
        final Subject<TestEvent> testEventSubject = new Subject<>();
        final TestObserver testObserver1 = new TestObserver();
        final TestObserver testObserver2 = new TestObserver();

        // Act
        testObserver1.observe(testEventSubject);
        testObserver2.observe(testEventSubject);

        final Note<TestEvent> testEventNote = new TestEvent("test");
        testEventSubject.observationStoppedThrough(testObserver1);
        testEventSubject.observationStoppedThrough(testObserver2);
        testEventSubject.notifyObserversWith(testEventNote);

        // Assert
        assertFalse(testObserver1.getSubject().isEmpty());
        assertFalse(testObserver2.getSubject().isEmpty());

        assertTrue(testObserver1.getNotifiedWithEvent() == null);
        assertTrue(testObserver2.getNotifiedWithEvent() == null);
    }

    @Getter
    public class TestObserver extends ObserverTemplate<TestEvent> {

        private Subject<TestEvent> notifiedBySubject = null;
        private Note<TestEvent> notifiedWithEvent = null;

        @Override
        public void takeNotice(
                @NonNull final Subject<TestEvent> subject,
                @NonNull final Note<TestEvent> event
        ) {
            notifiedBySubject = subject;
            notifiedWithEvent = event;

        }

        public List<Subject<TestEvent>> getSubject() {
            return super.subjects;
        }

    }

    @RequiredArgsConstructor
    public class TestEvent implements Note<TestEvent> {
        @NonNull
        private final String name;
    }

}
