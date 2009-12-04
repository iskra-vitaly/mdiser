## apply alpha coeffs to img basis images to render given light conditions

function [IMG] = castRamaLight(NN, ALPHA)
  BB = calcBasis(NN);
  B = BB(:,:);
  IMG = zeros(size(NN)(2:3));
  IMG(:) = B'*ALPHA;
end