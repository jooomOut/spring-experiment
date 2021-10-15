package me.jooomout.demorestapistudy.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data @Builder
public class AccountDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

}
