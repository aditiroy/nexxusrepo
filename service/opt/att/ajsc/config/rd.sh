#!/bin/bash
 
docker run -ti --rm \
    -p 8080:8080 \
    dockercentral.it.att.com:5100/com.att.salesmarketing.soma.pric/nexxus
