package me.jpomykala.oauth2.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Evelan on 17/12/2016.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GrantType {
    PASSWORD("password"), REFRESH_TOKEN("refresh_token");
    String value;
}
