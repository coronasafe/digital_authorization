package com.coronosafe.approval.docusign;

import com.coronosafe.approval.dto.DocuSignTokenDto;
import com.coronosafe.approval.dto.DocuSignUserInfoDto;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;

public interface DigiSignService {
    String getAuthCode(String stateString);

    DocuSignTokenDto getAccessToken(String authCode);

    DocuSignUserInfoDto getUserInfo(String accessToken);

    Object prepareDocumentSignature(String emailText, String emailIndex, DigiUser digiUser,
                                    String accessToken, DocuSignUserInfoDto docuSignUserInfoDto,
                                    String redirectState);

}
