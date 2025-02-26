package com.xaaef.tenancy.interceptor;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.xaaef.tenancy.util.JsonResult;
import com.xaaef.tenancy.util.JsonUtils;
import com.xaaef.tenancy.util.TenantUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.xaaef.tenancy.util.TenantUtils.X_TENANT_ID;


/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/11/15 11:41
 */


@Slf4j
@Component
@AllArgsConstructor
public class TenantIdInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var tenantId = request.getHeader(X_TENANT_ID);
        if (StringUtils.hasText(tenantId)) {
            log.info("preHandle Header ===> tenantId : {}", tenantId);
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } else {
            tenantId = request.getParameter(X_TENANT_ID);
            if (StringUtils.hasText(tenantId)) {
                log.info("preHandle Parameter ===> tenantId : {}", tenantId);
                TenantUtils.setTenantId(tenantId);
                return HandlerInterceptor.super.preHandle(request, response, handler);
            } else {
                renderString(response, String.format("HttpRequest' header must have %s", X_TENANT_ID));
                return false;
            }
        }
    }


    /**
     * 将字符串渲染到客户端
     */
    public static void renderString(HttpServletResponse response, String msg) {
        var failStr = JsonUtils.toJson(JsonResult.fail(msg));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            response.getWriter().print(failStr);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("renderString: {}", e.getMessage());
        }
    }


}

