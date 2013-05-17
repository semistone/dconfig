dconfig
=======

Config library with dimension concept

Inspire by Yahoo YCB library
https://npmjs.org/package/ycb
The config files structure and format is reference to  
https://github.com/yahoo/ycb/tree/master/tests

Now only implement command line to generate config file by different dimensions variable

### Download jar file
Jar file can download from http://angus-ec2.siraya.net/dconfig.jar

   
### Input structure
Input file only support yaml format now, and output format support yaml and ini format.
Must include at least one dimensions.yaml and many other yaml files.
If use ini format, the config structure only support two layer.
The example can be found in https://github.com/semistone/dconfig/tree/master/src/test/resources 
### dimensions.yaml
    dimensions: 
        environment:
            development:
            testing: 
            preproduction:
            production:
        site:
        device:
        lang:
            en:
                en_US: 
                en_GB: 
                en_CA: 
            fr:
                fr_FR:
                    fr_CA:
                      fr_Test:
### main.yaml     
    -   settings: master
        title_key: YRB_YAHOO
        data-url: http://service.yahoo.com
        logo: yahoo.png
        links: 
            home: http://www.yahoo.com
            mail: http://mail.yahoo.com
### sample1.yaml     
    -   settings: environment=development
        data-url: http://service_dev.yahoo.com
        links: 
            mail: http://mail_dev.yahoo.com

### How to use:
    java -jar dconfig.jar
    usage: dconfig
     -format <fotmat>   output format
     -in <in>           input file
     -out <out>         output file
     -query <query>     query string

###Command Example
    java -jar dconfig.jar -in example7 -format yaml -out example7.yaml -query 'lang=en'
    java -jar dconfig.jar -in example7 -format ini -out example7.yaml -query 'lang=en'
