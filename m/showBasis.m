## display nine basis images

function [size] = showBasis(BB)
  size = [size(BB, 2), size(BB, 3)];
  h = size(1);
  w = size(2);
  img = zeros(size);
  totalbasis = zeros(h*3, w*3);
  for i = 1:3
    for j = 1:3
      n = (i-1)*3+j;
      img(:,:) = BB(n,:,:);
      correction = min(min(img(:)), 0);
      img-= correction;
      correction = max(max(img(:)), 1);
      img/= correction;
      subplot(3, 3, n);
      imshow(img);
      totalbasis((i-1)*h+1:i*h, (j-1)*w+1:j*w)=img;
      imwrite(img, sprintf("basis.%d.jpg", n), "jpeg");
    end
  end
  imwrite(totalbasis, "totalbasis.jpg", "jpeg");
end