## display nine basis images

function [size] = showBasis(BB)
  size = [size(BB, 2), size(BB, 3)];
  img = zeros(size);
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
    end
  end
end