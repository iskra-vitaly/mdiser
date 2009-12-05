## display image in a grid with given illumination conditions

function displayLights(NN, L)
  n = rows(L);
  gridW = floor(sqrt(n));
  gridH = floor(n/gridW);

  for k=1:n
    subplot(gridW, gridH, k);
    showLighted(NN, L{k, 1}, L{k, 2});
  end
end 