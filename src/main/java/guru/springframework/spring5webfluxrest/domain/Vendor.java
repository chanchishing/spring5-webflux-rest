package guru.springframework.spring5webfluxrest.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Vendor {
    @Id
    String id;
    @Builder.Default
    String firstname="";
    @Builder.Default
    String lastname="";
}
