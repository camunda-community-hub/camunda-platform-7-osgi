export JAVA_HOME=$(/usr/libexec/java_home -v 1.6)
chmod 777 ./target/assembly/bin/karaf
./target/assembly/bin/karaf start
