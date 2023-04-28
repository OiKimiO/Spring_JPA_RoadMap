package hellojpa;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Embeddable
public class Period {

    private LocalDateTime startLocalDateTime;
    private LocalDateTime endLocalDateTime;

    public LocalDateTime getStartLocalDateTime() {
        return startLocalDateTime;
    }

    public void setStartLocalDateTime(LocalDateTime startLocalDateTime) {
        this.startLocalDateTime = startLocalDateTime;
    }

    public LocalDateTime getEndLocalDateTime() {
        return endLocalDateTime;
    }

    public void setEndLocalDateTime(LocalDateTime endLocalDateTime) {
        this.endLocalDateTime = endLocalDateTime;
    }

    public Period() {
    }
}
