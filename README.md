dconfig
=======

Config library with dimension concept

Config folder structure
---------
###dimension.yaml
    country:
       en:
          - uk
          - us
       zh:
          - tw
          - cn
          - hk
    environment:
        alpha:
        beta:
        staging:
            tp2:
            tpe:      
        production:
            tp2:
                farm1:
                farm2:
                farm3:
            tpe:      
                farm1:
                farm2:
                farm3:
    
###main.yaml
    var1: 123
    var2:
       var21: xyz
       var22: mmm

###en.yaml
    var1: 555

###en-us.yaml
    var1: 666

###alpha:
    var2:
       var21: 555

###beta:
    var1: 888

###production-tp2-farm1.yaml:
    var2:
       var21: 666
       var21: 777

API Behavior
---------
###If variable defined in sub dimension file
    DConfig config = new DConfig("/usr/local/etc/example/", "country=en-us;environment=alpha");
    Assert.equal(666, config.get("var1"));  #  from en-us.yaml
    Assert.equal(555, config.get("var2.var21")); # from alpha.yaml

###If not define in sub dimension file, get from main.yaml
    DConfig config = new DConfig("/usr/local/etc/example/", "country=en-uk;environment=alpha");
    Assert.equal('mmm', config.get("var2.var22"));  # from main.yaml
    Assert.equal(555, config.get("var1"));  # not found in en-uk, then search from en.yaml

###If Conflict happen, then order by calling sequence, for example "country=zh;environment=alpha"  country got hight priority.
If "environment=alpha;country=zh", then environment is higher priority.
    DConfig config = new DConfig("/usr/local/etc/example/", "country=en;environment=beta");
    Assert.equal(555, config.get("var1")); # from en.yaml
    DConfig config = new DConfig("/usr/local/etc/example/", "environment=beta;country=en");
    Assert.equal(888, config.get("var1")); # from beta.yaml


###Dimension can be very deep
    DConfig config = new DConfig("/usr/local/etc/example/", "environment=production-tp2-farm1");
    Assert.equal(666, config.get("var2.var21")); # production-tp2-farm1.yaml
