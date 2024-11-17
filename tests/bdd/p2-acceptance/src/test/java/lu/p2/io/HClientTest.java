package lu.p2.io;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class HClientTest {

    private OkHttpClient okHttpClient;
    private String siteUrl;
    private HClient hClient;

    @Before
    public void setUp() {
        okHttpClient = mock(OkHttpClient.class);
        siteUrl = String.format("https://%s", someAlphanumericString(10));
        hClient = new HClient(okHttpClient, siteUrl);
    }

    @Test
    public void uploadProfileImage() throws IOException {
        final Call call = mock(Call.class);
        final Response expected = mock(Response.class);

        // Given
        given(okHttpClient.newCall(any())).willReturn(call);
        given(call.execute()).willReturn(expected);

        // When
        final Response actual = hClient.uploadProfileImage(someString());

        // Then
        assertThat(actual, is(expected));
    }

}