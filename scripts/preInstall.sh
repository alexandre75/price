
#!/bin/sh
echo "Creating group: price"
/usr/sbin/groupadd -f -r price 2> /dev/null || :

echo "Creating user: price"
/usr/sbin/useradd -r -m -c "price user" price -g price 2> /dev/null || :
