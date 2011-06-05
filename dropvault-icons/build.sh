
rm -Rf 72x72 48x48 36x36 drawable-*;

sizes="72 48 36"

for size in $sizes;
do
    mkdir ${size}x${size};
    for icon in `ls ./svg/`;
    do
        iconPngName=$( echo $icon | cut -d . -f -1 )".png"
        inkscape --without-gui --export-png="./"${size}x${size}"/"$iconPngName --export-dpi=72 --export-background-opacity=0 --export-width=$size --export-height=$size "./svg/"$icon > /dev/null
    done
done

mv 36x36 drawable-ldpi;
mv 48x48 drawable-mdpi;
mv 72x72 drawable-hdpi;
