# A Rest Server

Has two endpoints:

### Product listing endpoint:

Method: GET   
Action: Get all products   
URI: /products   
Optional arguments:

- page:number, can be combined with the other arguments as well
- min_price:number & max_price:number
- category:string

### Product detail endpoint:

Method: GET   
Action: Get specific product by id    
URI: /products/{productid}   
