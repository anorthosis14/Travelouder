package travelouder.com.travelouder;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class AuthenticationHelper {

    private static GoogleSignInClient mGoogleSignInClient;

    // [END config_signin]



    public static GoogleSignInClient getmGoogleSignInClient(Context context){


        if (mGoogleSignInClient==null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        }
        return mGoogleSignInClient;

    }
}
