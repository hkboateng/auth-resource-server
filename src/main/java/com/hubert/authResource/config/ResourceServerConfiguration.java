package com.hubert.authResource.config;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import static java.nio.charset.StandardCharsets.UTF_8;
@Configuration
@EnableResourceServer
@EnableConfigurationProperties(SecurityProperties.class)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	 private static final String ROOT_PATTERN = "/**";
	 private static final String ROOT_PATTERN_TEST = "/test/**";
	    private final SecurityProperties securityProperties;

	    private TokenStore tokenStore;

	    public ResourceServerConfiguration(final SecurityProperties securityProperties) {
	        this.securityProperties = securityProperties;
	    }

	    @Override
	    public void configure(final ResourceServerSecurityConfigurer resources) {
	        resources.tokenStore(tokenStore());
	    }

	    @Override
	    public void configure(HttpSecurity http) throws Exception {
	        http.authorizeRequests()
	                .antMatchers(HttpMethod.GET, ROOT_PATTERN_TEST).access("#oauth2.hasScope('read')")
	                .antMatchers(HttpMethod.POST, ROOT_PATTERN).access("#oauth2.hasScope('write')")
	                .antMatchers(HttpMethod.PATCH, ROOT_PATTERN).access("#oauth2.hasScope('write')")
	                .antMatchers(HttpMethod.PUT, ROOT_PATTERN).access("#oauth2.hasScope('write')")
	                .antMatchers(HttpMethod.DELETE, ROOT_PATTERN).access("#oauth2.hasScope('write')");
	    }

	    @Bean
	    public DefaultTokenServices tokenServices(final TokenStore tokenStore) {
	        DefaultTokenServices tokenServices = new DefaultTokenServices();
	        tokenServices.setTokenStore(tokenStore);
	        return tokenServices;
	    }

	    @Bean
	    public TokenStore tokenStore() {
	        if (tokenStore == null) {
	            tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
	        }
	        return tokenStore;
	    }

	    @Bean
	    public JwtAccessTokenConverter jwtAccessTokenConverter() {
	        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	        converter.setVerifierKey(getPublicKeyAsString());
	        return converter;
	    }

	    private String getPublicKeyAsString() {
	    	Resource res = new ClassPathResource("pubKey.txt");
	        try {
	        	String publicKey = null;
	        	publicKey = IOUtils.toString(res.getInputStream(),UTF_8);
	        	return publicKey;
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }
}
