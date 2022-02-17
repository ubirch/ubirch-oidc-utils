# ubirch-oidc-utils
OpenID Connect related authorization utils

# testing
In case tests are failing, you might want to try to stop redis which 
has not been shut down properly by executing 'kill $(lsof -t -i:6379)'