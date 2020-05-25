if [ "$JAVA_HOME" == "" ]; then
  echo "JAVA_HOME is not set"
  exit 1
fi

if [ "$METMON_HOME" == "" ]; then
  METMON_HOME="$(dirname "$(dirname "$(readlink -fm "$0")")")"
fi
export METMON_HOME

# print the env
echo "METMON_HOME=$METMON_HOME"

# check whether tomcat is deployed.
if [ ! -d "$METMON_HOME/tomcat/bin" ]
then

  # save the current dir (useful if someone sourced this script)
  savedDir=$(pwd)

  echo "First time setup."
  echo "Extracting tomcat."
  cd "$METMON_HOME/tomcat"
  tar -xf "tomcat.tar.gz"
  mv apache*/* .
  rmdir apache*
  echo "Tomcat extracted."

  echo "Setting up webapp."
  cd "$METMON_HOME"
  warName=$(find "$METMON_HOME/lib" | grep 'metmon.rest')
  ln -s "$warName" "./tomcat/webapps/metmon.war"
  echo "Webapp setup done."

  # restore the current dir
  cd "$savedDir"
fi

"$METMON_HOME/tomcat/bin/startup.sh"
