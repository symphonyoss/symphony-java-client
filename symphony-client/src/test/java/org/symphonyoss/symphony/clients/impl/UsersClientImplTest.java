package org.symphonyoss.symphony.clients.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.pod.api.UserApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.AvatarUpdate;
import org.symphonyoss.symphony.pod.model.SuccessResponse;

import java.util.Random;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggerFactory.class, Configuration.class, UsersClientImpl.class })
public class UsersClientImplTest {

    private static final int IMAGE_LENGTH = 3;
    private static final String TOKEN_STRING = "tokenString";
    private static final String OK_RESPONSE = "OK";
    private static final String NOT_OK_RESPONSE = "NOT_OK";
    private static final long USER_ID = 123L;
    private static final String API_ERROR_COMMUNICATING_WITH_POD_WHILE_UPDATING_AVATAR = "API error communicating with POD, while updating avatar";
    private static final String AVATAR_UPDATE_FAILED = "Avatar update failed";
    private static final String POD_URL = "podUrl";

    private static Logger LOG;

    private UsersClientImpl usersClient;

    @Mock
    private ApiClient apiClientMock;

    @Mock
    private SymAuth symAuthMock;

    @Mock
    private UserApi userApiMock;

    @Mock
    private AvatarUpdate avatarUpdateMock;

    @Mock
    private SuccessResponse successResponseMock;

    @Mock
    private SymphonyClientConfig configMock;

    @Before
    public void before() throws Exception {
        mockLogger();
        mockConfiguration();

        whenNew(UserApi.class).withArguments(apiClientMock).thenReturn(userApiMock);

        whenNew(AvatarUpdate.class).withNoArguments().thenReturn(avatarUpdateMock);

        usersClient = new UsersClientImpl(symAuthMock, configMock, apiClientMock.getHttpClient());
    }

    @Test
    public void avatarArryIsNull() throws Exception {
        usersClient.updateUserAvatar(USER_ID, null);
        verifyNew(UserApi.class, times(0)).withArguments(eq(apiClientMock));
    }

    @Test
    public void successfulAvatarUpdate() throws ApiException, UsersClientException {
        mockSessionToken();
        mockUserApi(OK_RESPONSE);

        usersClient.updateUserAvatar(USER_ID, generateImageData());

        verify(LOG, times(0)).error(anyString(), any(Exception.class));

    }

    @Test(expected = UsersClientException.class)
    public void unsuccessfulAvatarUpdate() throws ApiException, UsersClientException {
        mockSessionToken();
        mockUserApi(NOT_OK_RESPONSE);

        usersClient.updateUserAvatar(USER_ID, generateImageData());

        verify(LOG, times(1)).error(eq(AVATAR_UPDATE_FAILED), any(IllegalStateException.class));
    }

    @Test(expected = UsersClientException.class)
    public void apiExceptionAvatarUpdate() throws ApiException, UsersClientException {
        mockSessionToken();
        mockUserApiException();

        usersClient.updateUserAvatar(USER_ID, generateImageData());

        verify(LOG, times(1)).error(eq(API_ERROR_COMMUNICATING_WITH_POD_WHILE_UPDATING_AVATAR),
                any(ApiException.class));
    }

    private void mockUserApi(String response) throws ApiException {
        when(userApiMock.v1AdminUserUidAvatarUpdatePost(TOKEN_STRING, USER_ID, avatarUpdateMock))
                .thenReturn(successResponseMock);
        when(successResponseMock.getMessage()).thenReturn(response);
    }

    private void mockUserApiException() throws ApiException {
        when(userApiMock.getApiClient()).thenReturn(apiClientMock);
        when(userApiMock.v1AdminUserUidAvatarUpdatePost(TOKEN_STRING, USER_ID, avatarUpdateMock))
                .thenThrow(new ApiException());
    }

    private void mockSessionToken() {
        Token token = new Token();
        token.setToken(TOKEN_STRING);
        when(symAuthMock.getSessionToken()).thenReturn(token);
    }

    private byte[] generateImageData() {
        byte[] imageData = new byte[IMAGE_LENGTH];
        new Random().nextBytes(imageData);
        return imageData;
    }

    private void mockConfiguration() {
        mockStatic(Configuration.class);
        when(Configuration.getDefaultApiClient()).thenReturn(apiClientMock);
    }

    private void mockLogger() {
        mockStatic(LoggerFactory.class);
        LOG = PowerMockito.mock(Logger.class);
        when(LoggerFactory.getLogger(UsersClientImpl.class)).thenReturn(LOG);
    }

}