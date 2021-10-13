#!/usr/bin/env python3
 
import requests, subprocess, cmd, sys

QUARKUS_RELEASE_URL='https://github.com/quarkusio/quarkus/releases/latest'
templates={'qkp':'quarkus-kafka-producer','qkc':'quarkus-kafka-consumer','qks':'quarkus-kafka-streams'}

def searchLatestRelease():
    # normally it should go to a redirection page
    releaseURL=requests.get(QUARKUS_RELEASE_URL).url
    words=releaseURL.split('/')
    return words[len(words) - 1]


def listTemplates():
    print("Template name: qkp  for 'Quarkus - Kafka producer'")
    print(templates)
    
   
def createProject(arg):
    args=arg.split(' ')
    templateFolder=templates[args[0]]
    appName=args[1]
    groupId=input('projectGroupId?: <ibm.eda.demo>') or "ibm.eda.demo"
    mvn="mvn io.quarkus:quarkus-maven-plugin:" + searchLatestRelease() + ":create -DprojectGroupId=" + groupId + " -DprojectArtifactId=" + appName + " -DclassName=\"" + groupId + ".app.api.GreetingResource\"   -Dpath=\"/greeting\" -Dextensions=\"resteasy,smallrye-reactive-messaging-kafka, smallrye-health, smallrye-openapi, kafka-client, openshift\""
    print(mvn)
    p = subprocess.Popen(mvn, shell=True)
    p.wait()
    subprocess.Popen("cp -r ./" + templateFolder + "/* ./" + appName, shell=True)
    subprocess.Popen("cp -r ./" + templateFolder + "/.github ./" + appName + "/", shell=True)
    print("Project should have been created under folder ./" + appName)

class EDAShell(cmd.Cmd):
    intro = "###############################\nWelcome to EDA project generator shell  Type help or ? to list commands.\n"
    prompt = '(ibm eda) '
    def do_listTemplates(self,arg):
        'List supported code templates'
        listTemplates()
    def do_bye(self, arg):
        'Stop recording, close the eda window, and exit:  BYE'
        print('Thank you for using EDA project generator')
        # self.close()
        return True
    def do_exit(self,arg):
        'Same as bye'
        self.do_bye(arg)
        return True
    def do_create(self,arg):
        'Create a project from an existing template. syntax is: create templatename appname\n ex: create qkp order-mgr'
        createProject(arg)

def parseArguments():
    appname="none"
    template="qks"
    if len(sys.argv) == 1:
        return (appname,template)
    else:
        for idx in range(1, len(sys.argv)):
            arg=sys.argv[idx]
            if arg == "--appname":
                appname=sys.argv[idx+1]
            elif arg == "--template":
                template=sys.argv[idx+1]
    return (appname,template)

if __name__ == "__main__":
    (appname,template) = parseArguments()
    if appname == "none":
        EDAShell().cmdloop()
    else:
        createProject(template + " " + appname)
