package org.lukawska.trainsmart.mailing.domain.valueObject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {

    @Getter
    @Column(nullable = false)
    private String fileName;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    public Attachment(String fileName, byte[] content) {
        this.fileName = Objects.requireNonNull(fileName);
        this.content = Objects.requireNonNull(content).clone();
    }

    public byte[] getContent() {
        return content.clone();
    }

    public long getSize() {
        return content.length;
    }
}
