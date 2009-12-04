## show lighted image

function [II] = showLighted(NN, L, W)
  II = weightedDiffuse(NN, L, W);
  imshow(II);
end