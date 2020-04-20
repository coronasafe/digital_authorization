package com.coronosafe.approval.constants;

public interface DigiConstants {
    String MISSION_DIRECTOR_ROLE="Mission Director";
    String INSTITUTION_ROLE ="Institution";
    String OFFICIAL_ROLE ="Government Official";

    String CURRENT_USER ="currentUser";
    String DIGI_SESSION_ATTR="sessionDto";

    String REQUEST_PARAM_ATTR="?";
    String REQUEST_PARAM_ATTR_SEPARATOR="&";
    String REQUEST_PARAM_ATTR_VALUE="=";

    String RESPONSE_TYPE="response_type";
    String RESPONSE_TYPE_VALUE="code";
    String SCOPE="scope";
    String SCOPE_VALUE="signature";
    String CLIENT_ID="client_id";
    String STATE="state";
    String REDIRECT_URI="redirect_uri";
    String login_hint="login_hint";

    String EMAIL_MISSION_DIRECTOR="A document has been sent for your review";
    String EMAIL_GOVERNMENT_AGENT="State Mission Director has reviewed a request to set up at CCC at so and so location";

    String URL_SEPERATOR="/";
    String JSON_USER_INFO="accounts";

    String CONTENT_TYPE="application/octet-stream";

    interface REGISTRATION {
        String ROLES_LIST="roleList";
        String REGISTER_USER="registerUser";
        String REGISTERED_USER="registeredUser";
    }
}
