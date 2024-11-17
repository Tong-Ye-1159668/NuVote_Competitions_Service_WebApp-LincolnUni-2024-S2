package lu.p2.io;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class HClient {

    private final OkHttpClient okHttpClient;
    private final String siteUrl;

    public HClient(final OkHttpClient okHttpClient, @Value("${site.url}") final String siteUrl) {
        this.okHttpClient = okHttpClient;
        this.siteUrl = siteUrl;
    }

    public Response uploadProfileImage(final String sessionValue) throws IOException {

        // Create the request body
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/profile.jpg")) {
            final RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("profile_image", "profile.jpg", RequestBody.create(inputStream.readAllBytes(), MediaType.parse("image/jpeg"))).build();

            // Build the request
            final Request request = new Request.Builder().url(String.format("%s/users/user/profile_image", siteUrl)).addHeader("Cookie", "session=" + sessionValue) // replace with your cookie name
                    .post(requestBody).build();

            // Execute the request
            try (Response response = okHttpClient.newCall(request).execute()) {
                return response;
            }
        }
    }
}