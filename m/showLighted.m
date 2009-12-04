## function [II] = showLighted(NN, L, W)
## -----show image with given light conditions 

function [II] = showLighted(NN, L, W)
  II = weightedDiffuse(NN, L, W);
  imshow(II);
end