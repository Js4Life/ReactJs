#/bin/bash


ng build --prod --aot

#upload files
# aws s3 cp ./dist s3://BUCKETNAME --recursive --acl public-read
aws s3 cp ./dist s3://vibmon --recursive 
