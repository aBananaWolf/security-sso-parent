package cn.com.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.OnCommittedResponseWrapper;

import javax.servlet.http.*;
import java.io.IOException;

/**
 * @author wyl
 * @create 2020-07-18 14:47
 */
@Slf4j
public class OnCommitResponseSecurityContextWrapper extends OnCommittedResponseWrapper {
    private SecurityContextRepository securityContextRepository;
    private HttpServletRequest request;

    public OnCommitResponseSecurityContextWrapper(HttpServletResponse response,
                                                  SecurityContextRepository securityContextRepository,
                                                  HttpServletRequest httpServletRequest
    ) {
        super(response);
        this.securityContextRepository = securityContextRepository;
        this.request = httpServletRequest;
    }

    @Override
    protected void onResponseCommitted() {
        if (SecurityContextHolder.getContext() != null)
            try {
                // 本身确保了每个请求只被调用一次
                this.securityContextRepository.saveContext(SecurityContextHolder.getContext(), this.request,this);
            } catch (Exception e) {
                log.info("save securityContext failed");
            }
    }
}
