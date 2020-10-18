# How to update your keystore
To allow developer access to admin login page each developer must use the same keystore which should be located on your computer at **~/.android/debug.keystore** or **C:\Users\{{profile_name}}\.android** and the keystore which will be placed on your computer can be found inside code repository at keystore/.

# Why we have a common keystore
We need to use a common keystore as a different keystore will change the output:
> gradle signingReport

Which is associated with the oAuth2 client “OAuth client” found in the developer console.

# The SHA-1 Certificate
SHA1: E3:05:18:EE:E8:04:15:1C:11:A1:99:2E:1E:75:DD:49:41:8A:F3:AE

Google Drive Document Link:
https://docs.google.com/document/d/1r7czHC4SbMWacug2GTonSk3JWZq9KW69xrhkbV0TXYA/edit?usp=sharing