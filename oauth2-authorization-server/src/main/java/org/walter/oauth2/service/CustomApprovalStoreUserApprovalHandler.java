package org.walter.oauth2.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义在授权页面中触发授权选项（同意/拒绝）后，要额外执行的动作
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class CustomApprovalStoreUserApprovalHandler extends ApprovalStoreUserApprovalHandler {
    @Setter
    private ApprovalStoreUserApprovalHandler approvalStoreUserApprovalHandler;

    @Override
    public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        AuthorizationRequest result = approvalStoreUserApprovalHandler.updateAfterApproval(authorizationRequest, userAuthentication);

        try{
            if(result.isApproved()){
                doAfterApproved(result, userAuthentication);
            }else{
                doAfterDenied(result, userAuthentication);
            }
        }catch (Exception e){
            log.error("[updateAfterApproval] Fail", e);
        }

        return result;
    }

    /**
     * 选择“同意”后执行
     * @param authorizationRequest
     * @param userAuthentication
     * @throws Exception
     */
    protected void doAfterApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) throws Exception {
    }

    /**
     * 选择“拒绝”后执行
     * @param authorizationRequest
     * @param userAuthentication
     * @throws Exception
     */
    protected void doAfterDenied(AuthorizationRequest authorizationRequest, Authentication userAuthentication) throws Exception{
    }

    private HttpServletResponse getCurrentResponse(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    @Override
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        approvalStoreUserApprovalHandler.setClientDetailsService(clientDetailsService);
    }

    @Override
    public void setScopePrefix(String scopePrefix) {
        approvalStoreUserApprovalHandler.setScopePrefix(scopePrefix);
    }

    @Override
    public void setApprovalStore(ApprovalStore store) {
        approvalStoreUserApprovalHandler.setApprovalStore(store);
    }

    @Override
    public void setRequestFactory(OAuth2RequestFactory requestFactory) {
        approvalStoreUserApprovalHandler.setRequestFactory(requestFactory);
    }

    @Override
    public void setApprovalExpiryInSeconds(int approvalExpirySeconds) {
        approvalStoreUserApprovalHandler.setApprovalExpiryInSeconds(approvalExpirySeconds);
    }

    @Override
    public void afterPropertiesSet() {
        approvalStoreUserApprovalHandler.afterPropertiesSet();
    }

    @Override
    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        return approvalStoreUserApprovalHandler.isApproved(authorizationRequest, userAuthentication);
    }

    @Override
    public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        return approvalStoreUserApprovalHandler.checkForPreApproval(authorizationRequest, userAuthentication);
    }

    @Override
    public Map<String, Object> getUserApprovalRequest(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        return approvalStoreUserApprovalHandler.getUserApprovalRequest(authorizationRequest, userAuthentication);
    }
}
