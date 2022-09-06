package de.bs.webauthenticationsystem_be.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptsService {
  private static final int NUMBER_OF_ATTEMPTS = 3;
  private static final int LOCKED_TIME = 1;
  private LoadingCache<String, Integer> loadingAttemptCache;

  public LoginAttemptsService() {
      super();
    loadingAttemptCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(LOCKED_TIME, MINUTES)
            .maximumSize(100)
            .build(
                new CacheLoader<String, Integer>() {
                  @Override
                  public Integer load(String key) throws Exception {
                    return 0;
                  }
                });
  }

  public void removeUserAttemptsFromCache(String username) {
    loadingAttemptCache.invalidate(username);
  }

  public void addUserAttemptToCache(String username)  {
      int attempts = 0;
      try {
          attempts = 1 + loadingAttemptCache.get(username);
      } catch (ExecutionException e) {
          e.printStackTrace();
      }
      loadingAttemptCache.put(username, attempts);
  }

  public boolean usernameOverpassMaxAttempts(String username)  {
      try {
          return loadingAttemptCache.get(username) >= NUMBER_OF_ATTEMPTS;
      } catch (ExecutionException e) {
          e.printStackTrace();
      }
return false;
  }

}
