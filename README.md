dconfig
=======

Config library with dimension concept

Config folder structure
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

###production-tp2-farm1.yaml:
    var2:
       var21: 666
       var21: 777
