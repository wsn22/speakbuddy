## Assumptions

In this task, we have audio processor to convert audio from
wma (allowed POST filetype) and save it as wma only.

Maybe, on real case we need to save on multiple format so mobile apps can 
decide which data type that suit their needs. 

## How to run

### TODO
Add more layer between controller and service
Find out howto simplify audio converter so it become more modular

Cleaning out temp files. If we don't use any storage with lifecycle control,
then I think we should create another java app (to have better control and logging)
to clean out temp files.