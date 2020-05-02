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

# find the jar
jarFile=$(find "$METMON_HOME/lib" -name 'metmon.rest*')

# execute it
"$JAVA_HOME/bin/java" -jar "$jarFile"
