#!/c/Windows/System32/WindowsPowerShell/v1.0/powershell.exe -File

# DEFINE INPUT PARAMETER --------------------------------------------------------------------------------------------- #
$DOCKER_VERSION_TAG = $args[0]

## validate input parameters
if ($args.count -ne 1) {
  echo "Invalid nr of arguments supplied; Expected <DOCKER_VERSION_TAG>"
  exit 255
}

# END DEFINE INPUT PARAMETER ------------------------------------------------------------------------------------- END #

# ==> MAIN LOGIC <== #
docker build -t "svencc/recom-base:$DOCKER_VERSION_TAG" .
docker tag "svencc/recom-base:$DOCKER_VERSION_TAG" "svencc/recom-base:$DOCKER_VERSION_TAG"
docker push "svencc/recom-base:$DOCKER_VERSION_TAG"

echo "svencc/recom-base:$DOCKER_VERSION_TAG"
exit 0
