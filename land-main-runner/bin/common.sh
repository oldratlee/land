OS_TYPE=$(uname | tr A-Z a-z)

#######################################
# Util Functions 
#######################################

colorEcho() {
    local color=$1
    shift
    if [ -c /dev/stdout ] ; then
        # if stdout is console, turn on color output.
        echo -ne "\033[1;${color}m"
        echo -n "$@"
        echo -e "\033[0m"
    else
        echo "$@"
    fi
}

redEcho() {
    colorEcho 31 "$@"
}

greenEcho() {
    colorEcho 32 "$@"
}

yellowEcho() {
    colorEcho 33 "$@"
}

blueEcho() {
    colorEcho 34 "$@"
}

echoCmdLineThenRun() {
    echo ========================================================
    blueEcho "run command:" 
    echo "$@"
    echo ========================================================
    "$@"
}

# exit if root run the script
checkRootUser() {
    `id -u` = 0 && { 
        redEcho "****************************************************"
        redEcho "*Error: root (the superuser) can't run this script.*"
        redEcho "****************************************************"
        exit 1
    }
}

findJavaBin() {
    [ -n "$JAVA_HOME" ] && {
        echo "$JAVA_HOME/bin/java"
    } || {
        which java > /dev/null && {
            echo java
        } || {
            redEcho "Error: fail to find java executable!"
            exit 2
        }
    }
}

genClassPathOfJarsInDirs() {
    local d
    local classpath
    for d in "$@"; do
        local jar
        for jar in $(echo "$d/*.jar"); do
            case "${OS_TYPE}" in
            cygwin*)
                [ -z "$classpath" ] && classpath="$jar" || classpath+=";$(cygpath -w "$jar")"
                ;;
            *)
                [ -z "$classpath" ] && classpath="$jar" || classpath+=":$jar"
                ;;
            esac
        done
    done
}

#######################################
# common logic 
#######################################

