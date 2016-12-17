package me.jpomykala.oauth2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import me.jpomykala.oauth2.model.user.GrantType;
import me.jpomykala.oauth2.model.user.Scope;
import me.jpomykala.oauth2.model.user.User;
import me.jpomykala.oauth2.model.user.UserRole;
import me.jpomykala.oauth2.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Evelan on 17/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OAuthExampleApplication.class})
public class OAuthTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private User userWithUserRole;
    private User userWithAdminRole;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        userRepository.deleteAll();
        createUserWithRoleUser();
        createUserWithRoleAdmin();
    }

    private void createUserWithRoleUser() {
        userWithUserRole = User.builder()
                .email("evelan@gmail.com")
                .password("dupa")
                .enabled(Boolean.TRUE)
                .clientId("845768247302409")
                .secret("69874309525827432")
                .scopes(Sets.newHashSet(Scope.POSTS))
                .grantTypes(Sets.newHashSet(GrantType.PASSWORD))
                .userRoles(Sets.newHashSet(UserRole.ROLE_USER))
                .firstName("Jakub").build();
        userWithUserRole = userRepository.save(userWithUserRole);
    }

    private void createUserWithRoleAdmin() {
        userWithAdminRole = User.builder()
                .email("evelan_admin@gmail.com")
                .password("admin_dupa")
                .enabled(Boolean.TRUE)
                .clientId("746578923")
                .secret("894573802470923")
                .scopes(Sets.newHashSet(Scope.POSTS))
                .grantTypes(Sets.newHashSet(GrantType.PASSWORD))
                .userRoles(Sets.newHashSet(UserRole.ROLE_ADMIN))
                .firstName("James").build();
        userWithAdminRole = userRepository.save(userWithAdminRole);
    }

    @Test
    public void unauthorized_pass() throws Exception {
        mockMvc.perform(get("/hello")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void requestAuthenticatedEndpoint_pass() throws Exception {
        String accessTokenWithType = getAccessTokenWithType(userWithUserRole);
        mockMvc.perform(get("/hello")
                .header("Authorization", accessTokenWithType)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void unauthorized_get_posts_pass() throws Exception {
        mockMvc.perform(get("/api/posts")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void authorized_with_user_role_get_posts_pass() throws Exception {
        String accessTokenWithType = getAccessTokenWithType(userWithUserRole);
        mockMvc.perform(get("/api/posts")
                .header("Authorization", accessTokenWithType)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void authorized_without_user_role_get_posts_pass() throws Exception {
        String accessTokenWithType = getAccessTokenWithType(userWithAdminRole);
        mockMvc.perform(get("/api/posts")
                .header("Authorization", accessTokenWithType)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    public void authorize_and_get_token_pass() throws Exception {
        String clientId = userWithUserRole.getClientId();
        String secret = userWithUserRole.getSecret();
        String username = userWithUserRole.getUsername();
        String password = userWithUserRole.getPassword();

        Map<String, String> accessTokenObject = getAuthorizationResponse(username, password, clientId, secret);
        Assert.assertNotNull(accessTokenObject);

        String accessToken = accessTokenObject.get("access_token");
        Assert.assertNotNull(accessToken);

        String accessTokenType = accessTokenObject.get("token_type");
        Assert.assertEquals("bearer", accessTokenType);

        String scope = accessTokenObject.get("scope");
        Assert.assertEquals("posts", scope);

        String expireTime = accessTokenObject.get("expires_in");
        Assert.assertNotNull(expireTime);
        Assert.assertTrue(Integer.valueOf(expireTime) > 0);
    }


    private String getAccessTokenWithType(User user) throws Exception {
        String clientId = user.getClientId();
        String secret = user.getSecret();
        String username = user.getUsername();
        String password = user.getPassword();

        Map<String, String> accessTokenObject = getAuthorizationResponse(username, password, clientId, secret);
        String accessToken = accessTokenObject.get("access_token");
        String accessTokenType = accessTokenObject.get("token_type");
        return accessTokenType + " " + accessToken;
    }

    public Map<String, String> getAuthorizationResponse(String username, String password, String clientId, String secret) throws Exception {
        byte[] clientIdAndSecretBytes = (clientId + ":" + secret).getBytes();
        String authorization = "Basic " + new String(Base64Utils.encode(clientIdAndSecretBytes));

        String stringResponse =
                mockMvc.perform(post("/oauth/token")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", password)
                        .param("grant_type", "password")
                        .param("scope", "posts")
                        .param("client_id", clientId)
                        .param("client_secret", secret))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(stringResponse, new TypeReference<Map<String, String>>() {
        });
    }


}
